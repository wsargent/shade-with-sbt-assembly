package example;

import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;
import shadeio.net.bytebuddy.ByteBuddy;
import shadeio.net.bytebuddy.agent.ByteBuddyAgent;
import shadeio.net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import shadeio.net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static shadeio.net.bytebuddy.matcher.ElementMatchers.named;

public class Hello {
    public static void main(String[] args) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ByteBuddyAgent.install();

        Config config = ConfigFactory.load();
        String someValue = config.getString("some.key");
        System.out.printf("hello: someValue = %s\n", someValue);

        Source source = new ByteBuddy()
                .subclass(Source.class)
                .method(named("hello")).intercept(MethodDelegation.to(Target.class))
                .make()
                .load(Hello.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded()
                .getDeclaredConstructor()
                .newInstance();

        System.out.println(source.hello("World"));
    }

    @SuppressWarnings("unused")
    public static class Source {

        public String hello(String name) {
            return null;
        }
    }

    @SuppressWarnings("unused")
    public static class Target {

        public static String hello(String name) {
            return "Hello " + name + "!";
        }
    }
}
