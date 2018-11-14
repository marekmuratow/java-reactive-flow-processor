package pl.entito;

import java.util.concurrent.Flow.Processor;

public class ReactiveProcessorMain {

	public static void main(String[] args) {
		Processor processor = new CustomProcessor();
		processor.subscribe(processor);
	}

}
