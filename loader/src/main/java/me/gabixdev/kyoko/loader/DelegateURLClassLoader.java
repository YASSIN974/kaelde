package me.gabixdev.kyoko.loader;

import java.net.URL;
import java.net.URLClassLoader;

public class DelegateURLClassLoader extends URLClassLoader {
    private final ClassLoader delegate;

    public DelegateURLClassLoader(URL[] urls, ClassLoader delegate) {
        super(urls, null);
        this.delegate = delegate;
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return super.loadClass(name);
        } catch (Exception e) {
            return delegate.loadClass(name);
        }
    }
}
