package org.spring4speedtracer.example.service.child;

import org.speedtracer.annotation.Traceable;
import org.springframework.stereotype.Service;

/**
 * Implementation of example slow service.
 * 
 * @author Dustin
 * 
 */
@Service("slowService")
@Traceable
public class SlowServiceImpl implements SlowService {

	@Override
	public String runSlow(String arg) {
		try {
			Thread.sleep(1000L);
		} catch (InterruptedException e) {
		}
		return arg;
	}

}
