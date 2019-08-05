package com.trendyol.scheduler;

import com.trendyol.scheduler.utils.Clock;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.format.DateTimeFormat;
import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface FrozenClock {

    String value();

    String format() default "dd/MM/yyyy";

    class Rule implements TestRule {

        @Override
        public Statement apply(Statement statement, Description description) {
            FrozenClock annotation = description.getAnnotation(FrozenClock.class);
            if (annotation == null) {
                return statement;
            }
            return new Statement() {
                @Override
                public void evaluate() throws Throwable {
                    try {
                        DateTime dateTime = DateTime.parse(annotation.value(), DateTimeFormat.forPattern(annotation.format()));
                        DateTime asUTC = dateTime.withZoneRetainFields(DateTimeZone.UTC);
                        Clock.freeze(asUTC.toDateTime());
                        statement.evaluate();
                    } finally {
                        Clock.unfreeze();
                    }
                }
            };
        }

    }
}
