package moe.kyokobot.bot.util;
/*
 * Copyright 2016-2018 John Grosh (jagrosh) & Kaidan Gustave (TheMonitorLizard)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.ShutdownEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import net.dv8tion.jda.core.hooks.SubscribeEvent;
import net.dv8tion.jda.core.utils.Checks;

import java.util.Set;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class EventWaiter implements EventListener
{
    private final ConcurrentHashMap<Class<?>, ConcurrentSkipListSet<WaitingEvent>> waitingEvents;
    private final ScheduledExecutorService threadpool;
    private final boolean shutdownAutomatically;

    /**
     * Constructs an empty EventWaiter.
     */
    public EventWaiter()
    {
        this(Executors.newSingleThreadScheduledExecutor(), true);
    }

    public EventWaiter(ScheduledExecutorService threadpool, boolean shutdownAutomatically)
    {
        Checks.notNull(threadpool, "ScheduledExecutorService");
        Checks.check(!threadpool.isShutdown(), "Cannot construct EventWaiter with a closed ScheduledExecutorService!");

        this.waitingEvents = new ConcurrentHashMap<>();
        this.threadpool = threadpool;
        this.shutdownAutomatically = shutdownAutomatically;
    }

    public boolean isShutdown()
    {
        return threadpool.isShutdown();
    }

    /**
     * Waits an indefinite amount of time for an {@link net.dv8tion.jda.core.events.Event Event} that
     * returns {@code true} when tested with the provided {@link java.util.function.Predicate Predicate}.
     *
     * <p>When this occurs, the provided {@link java.util.function.Consumer Consumer} will accept and
     * execute using the same Event.
     *
     * @param  <T>
     *         The type of Event to wait for.
     * @param  classType
     *         The {@link java.lang.Class} of the Event to wait for. Never null.
     * @param  condition
     *         The Predicate to test when Events of the provided type are thrown. Never null.
     * @param  action
     *         The Consumer to perform an action when the condition Predicate returns {@code true}. Never null.
     *
     * @throws IllegalArgumentException
     *         One of two reasons:
     *         <ul>
     *             <li>1) Either the {@code classType}, {@code condition}, or {@code action} was {@code null}.</li>
     *             <li>2) The internal threadpool is shut down, meaning that no more tasks can be submitted.</li>
     *         </ul>
     */
    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action)
    {
        waitForEvent(classType, condition, action, -1, null, null);
    }

    /**
     * Waits a predetermined amount of time for an {@link net.dv8tion.jda.core.events.Event Event} that
     * returns {@code true} when tested with the provided {@link java.util.function.Predicate Predicate}.
     *
     * <p>Once started, there are two possible outcomes:
     * <ul>
     *     <li>The correct Event occurs within the time allotted, and the provided
     *     {@link java.util.function.Consumer Consumer} will accept and execute using the same Event.</li>
     *
     *     <li>The time limit is elapsed and the provided {@link java.lang.Runnable} is executed.</li>
     * </ul>
     *
     * @param  <T>
     *         The type of Event to wait for.
     * @param  classType
     *         The {@link java.lang.Class} of the Event to wait for. Never null.
     * @param  condition
     *         The Predicate to test when Events of the provided type are thrown. Never null.
     * @param  action
     *         The Consumer to perform an action when the condition Predicate returns {@code true}. Never null.
     * @param  timeout
     *         The maximum amount of time to wait for, or {@code -1} if there is no timeout.
     * @param  unit
     *         The {@link java.util.concurrent.TimeUnit TimeUnit} measurement of the timeout, or
     *         {@code null} if there is no timeout.
     * @param  timeoutAction
     *         The Runnable to run if the time runs out before a correct Event is thrown, or
     *         {@code null} if there is no action on timeout.
     *
     * @throws IllegalArgumentException
     *         One of two reasons:
     *         <ul>
     *             <li>1) Either the {@code classType}, {@code condition}, or {@code action} was {@code null}.</li>
     *             <li>2) The internal threadpool is shut down, meaning that no more tasks can be submitted.</li>
     *         </ul>
     */
    public <T extends Event> void waitForEvent(Class<T> classType, Predicate<T> condition, Consumer<T> action,
                                               long timeout, TimeUnit unit, Runnable timeoutAction)
    {
        Checks.check(!isShutdown(), "Attempted to register a WaitingEvent while the EventWaiter's threadpool was already shut down!");
        Checks.notNull(classType, "The provided class type");
        Checks.notNull(condition, "The provided condition predicate");
        Checks.notNull(action, "The provided action consumer");

        WaitingEvent we = new WaitingEvent<>(condition, action);
        Set<WaitingEvent> set = waitingEvents.computeIfAbsent(classType, c -> new ConcurrentSkipListSet<>());
        set.add(we);

        if(timeout > 0 && unit != null)
        {
            threadpool.schedule(() ->
            {
                if(set.remove(we) && timeoutAction != null)
                    timeoutAction.run();
            }, timeout, unit);
        }
    }

    @Override
    @SubscribeEvent
    @SuppressWarnings("unchecked")
    public final void onEvent(Event event)
    {
        Class c = event.getClass();

        // Runs at least once for the fired Event, at most
        // once for each superclass (excluding Object) because
        // Class#getSuperclass() returns null when the superclass
        // is primitive, void, or (in this case) Object.
        while(c != null)
        {
            if(waitingEvents.containsKey(c))
            {
                Set<WaitingEvent> set = waitingEvents.get(c);
                WaitingEvent[] toRemove = set.toArray(new WaitingEvent[set.size()]);

                // WaitingEvent#attempt invocations that return true have passed their condition tests
                // and executed the action. We filter the ones that return false out of the toRemove and
                // remove them all from the set.
                set.removeAll(Stream.of(toRemove).filter(i -> i.attempt(event)).collect(Collectors.toSet()));
            }
            if(event instanceof ShutdownEvent && shutdownAutomatically)
            {
                threadpool.shutdown();
            }
            c = c.getSuperclass();
        }
    }

    /**
     * Closes this EventWaiter if it doesn't normally shutdown automatically.
     *
     * <p><b>IF YOU USED THE DEFAULT CONSTRUCTOR WITH NO ARGUMENTS DO NOT CALL THIS!</b>
     * <br>Calling this method on an EventWaiter that does shutdown automatically will result in
     * an {@link java.lang.UnsupportedOperationException UnsupportedOperationException} being thrown.
     *
     * @throws UnsupportedOperationException
     *         The EventWaiter is supposed to close automatically.
     */
    public void shutdown()
    {
        if(shutdownAutomatically)
            throw new UnsupportedOperationException("Shutting down EventWaiters that are set to automatically close is unsupported!");

        threadpool.shutdown();
    }

    private class WaitingEvent<T extends Event>
    {
        final Predicate<T> condition;
        final Consumer<T> action;

        WaitingEvent(Predicate<T> condition, Consumer<T> action)
        {
            this.condition = condition;
            this.action = action;
        }

        boolean attempt(T event)
        {
            if(condition.test(event))
            {
                action.accept(event);
                return true;
            }
            return false;
        }
    }
}
