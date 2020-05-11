package example.utils;

import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author camille.walim
 * 
 * Remove some verbose instructions : preconditions, unchecked, etc.
 */
public class NonVerbose {
	
	@FunctionalInterface
	public interface FException<I,O> {
		O apply(I t) throws Throwable;		
	}
	@FunctionalInterface
	public interface CException<I> {
		void accept(I t) throws Throwable;		
	}
	@FunctionalInterface
	public interface RException {
		void run() throws Throwable;		
	}
	public static <I,O> Function<I,O>  uncheckedF(FException<I,O> f){
		return i -> {
            try {
                return f.apply(i);
            }catch (Throwable e) {
                throw new RuntimeException(e);
            }
		};
	}
	public static <I> Consumer<I>  uncheckedC(CException<I> f){
		return i -> {
            try {
                 f.accept(i);
            }catch (Throwable e) {
                throw new RuntimeException(e);
            }
		};
	}
	public static <I> Runnable uncheckedR(RException f){
		return () -> {
            try {
                 f.run();
            }catch (Throwable e) {
                throw new RuntimeException(e);
            }
		};
	}
	
	public static <T> T impossible(String why) {
		throw new RuntimeException(why);
	}

}
