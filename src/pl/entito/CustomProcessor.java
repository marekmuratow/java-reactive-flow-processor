package pl.entito;

import java.util.Iterator;
import java.util.Optional;
import java.util.concurrent.Flow;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;
import java.util.stream.IntStream;

public class CustomProcessor implements Flow.Processor<Integer, String> {

	public static final int DEMAND = 1;
	private Subscription subscription;
	private Subscriber<? super String> subscriber;
	private Optional<Integer> nextValue;

	@Override
	public void onSubscribe(Subscription subscription) {
		System.out.println("onSubscribtion");
		subscription.request(DEMAND);
		if (nextValue.isPresent()) {
			this.onNext(nextValue.get());
		}
	}

	@Override
	public void onNext(Integer item) {
		Optional<Integer> localItem = Optional.ofNullable(item);

		while (localItem.isPresent()) {
			System.out.println("processing " + localItem.get() + " -> " + (localItem.get() + 100));
			subscription.request(DEMAND);
			localItem = nextValue;
		}
		subscription.cancel();
	}

	@Override
	public void onError(Throwable throwable) {
		System.out.println("Error " + throwable.getMessage());
	}

	@Override
	public void onComplete() {
		System.out.println("Completed");

	}

	@Override
	public void subscribe(Subscriber<? super String> subscriber) {
		System.out.println("subscribe");
		this.subscriber = subscriber;
		this.subscription = new CustomSubscription();
		this.subscriber.onSubscribe(subscription);
	}

	private class CustomSubscription implements Flow.Subscription {
		Iterator<Integer> values;

		private CustomSubscription() {
			values = IntStream.range(0, 100).boxed().iterator();
		}

		@Override
		public void request(long n) {
			if (values.hasNext()) {
				nextValue = Optional.of(values.next());
			} else {
				nextValue = Optional.empty();
			}
		}

		@Override
		public void cancel() {
			values = null;
			System.out.println("Subscritpion cancelled");
		}
	}

}
