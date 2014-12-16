package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.Behaviour;

@SuppressWarnings("serial")
public class BidderManagementBehaviour extends Behaviour
{
	public BidderManagementBehaviour(MarketAgent myMarketAgent)
	{
	}
	
	@Override
	public void action()
	{
	}

	@Override
	public boolean done()
	{
		MarketAgent myMarketAgent =
				(MarketAgent) myAgent;
		
		return myMarketAgent.isDone();
	}
}
