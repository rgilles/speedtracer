package org.spring4speedtracer.example.service;

import org.speedtracer.annotation.Traceable;
import org.spring4speedtracer.example.service.child.FastService;
import org.spring4speedtracer.example.service.child.SlowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

/**
 * Implementation of example top-level service.
 * 
 * @author Dustin
 * 
 */
@Service("mainService")
@Traceable
public class MainServiceImpl implements MainService {

	private final FastService fastService;
	private final SlowService slowService;

    @Inject
    public MainServiceImpl(FastService fastService, SlowService slowService) {
        this.fastService = fastService;
        this.slowService = slowService;
    }


    @Override
	public void fastMethod() {
		fastService.runFast();
		fastService.runFast();
		fastService.runFast();
	}

	@Override
	public String slowMethod(String arg) {
		return slowService.runSlow(arg);
	}
}
