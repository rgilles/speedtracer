package org.spring4speedtracer.example.service.child;

import org.speedtracer.annotation.Traceable;
import org.springframework.stereotype.Service;

/**
 * Implementation of example fast service.
 * 
 * @author Dustin
 * 
 */
@Service("fastService")
@Traceable
public class FastServiceImpl implements FastService {

	@Override
	public void runFast() {
		try {
			Thread.sleep(100L);
		} catch (InterruptedException e) {
		}
	}
}
