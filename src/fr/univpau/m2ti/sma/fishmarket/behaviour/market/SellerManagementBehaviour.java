package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.Behaviour;

@SuppressWarnings("serial")
public class SellerManagementBehaviour extends Behaviour
{
	public SellerManagementBehaviour(MarketAgent myMarketAgent)
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
