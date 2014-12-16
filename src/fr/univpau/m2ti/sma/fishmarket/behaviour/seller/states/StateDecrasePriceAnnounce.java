package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states;

import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
public class StateDecrasePriceAnnounce extends OneShotBehaviour {

	@Override
	public void action() {
		// TODO Auto-generated method stub
		System.out.println("action => " + getBehaviourName());
	}

	@Override
	public int onEnd()
	{
		System.out.println("onEnd => " + getBehaviourName());
		
		return 0;
	}
}
