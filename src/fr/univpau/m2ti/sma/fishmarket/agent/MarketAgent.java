package fr.univpau.m2ti.sma.fishmarket.agent;

import jade.core.Agent;

@SuppressWarnings("serial")
public class MarketAgent extends Agent
{
	public static final String SERVICE = "FISH_MARKET_SERVICE";
	
	@Override
	protected void setup()
	{
		// Register service to DF
	}
	
	@Override
	protected void takeDown()
	{
		// De-register service from DF
	}
}
