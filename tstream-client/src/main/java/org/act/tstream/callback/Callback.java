package org.act.tstream.callback;

/**
 * Callback interface
 * 
 * @author lixin 2012-3-12
 * 
 */
public interface Callback {

	public <T> Object execute(T... args);

}
