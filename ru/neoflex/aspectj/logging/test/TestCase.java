package ru.neoflex.aspectj.logging.test;

import java.util.Arrays;
import java.util.List;

import ru.neoflex.aspectj.logging.Finest;
import ru.neoflex.aspectj.logging.Logged;
import ru.neoflex.aspectj.logging.NoLog;

@Logged
public class TestCase {


    @NoLog
    public static void main(String[] args) {
        System.out.println("\nSysout: " + new TestCase().doTheJob("inStr", 12, null));

        try{
            new TestCase().doSomethingElse(new String[]{"aabb", "aaaaa"}, Arrays.asList(new String[]{"----"}),"param ann test", "wwew");    
        }catch (Exception e) {
            
        }
        
    }

    public String doTheJob(String in, int moreInt, Object foo) {
        return "resp " + moreInt;
    }

    public void doSomethingElse(@Finest String[] t, List<String> t2, @Finest String parameter, @MyAnnotation(name = "aName", value = "aValue")
    String p2) {
        throw new IllegalArgumentException("Smth terrible happened.");

    }

}
