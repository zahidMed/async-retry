package org.digibooster.retry.listener;

/**
 * Interface for listener that can be used to detect retry events during its lifecycle
 * @param <T> the type of object returned by the target method.
 *
 * @author Mohammed ZAHID {@literal <}zahid.med@gmail.com{@literal >}
 */
public interface AsyncRetryableListener<T> {

	/**
	 * Called before each retry attempt including the first direct call of the target method.
	 * @param retryCount the number of retries. 0 means the first direct call
	 * @param args the target method arguments
	 */
	void beforeRetry(Integer retryCount, Object[] args);

	/**
	 * Called after each retry attempt including the first direct call of the target method.
	 * @param retryCount the number of retries. 0 means the first direct call
	 * @param result the Object returned by the target method if no exception is thrown
	 * @param args the target method arguments
	 * @param e the exception if thrown
	 */
	void afterRetry(Integer retryCount,T result, Object[] args, Throwable e);

	/**
	 * Called when the retry reaches the max attempt count
	 * @param args the target method arguments
	 * @param e the exception if thrown
	 */
	void onRetryEnd(Object[] args, Throwable e);

}
