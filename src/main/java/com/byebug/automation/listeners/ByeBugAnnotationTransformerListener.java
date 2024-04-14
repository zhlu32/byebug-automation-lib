package com.byebug.automation.listeners;

import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;
import org.testng.internal.annotations.DisabledRetryAnalyzer;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class ByeBugAnnotationTransformerListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        // Auto set retryAnalyzer to all test method
        Class<? extends IRetryAnalyzer> retry = annotation.getRetryAnalyzerClass();
        if (retry == null || DisabledRetryAnalyzer.class.equals(retry)) {
            annotation.setRetryAnalyzer(ByeBugRetryAnalyzer.class);
        }
    }

}

