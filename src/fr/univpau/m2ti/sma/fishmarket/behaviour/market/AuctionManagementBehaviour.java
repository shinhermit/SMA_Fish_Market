package fr.univpau.m2ti.sma.fishmarket.behaviour.market;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.FSMBehaviour;

@SuppressWarnings("serial")
public class AuctionManagementBehaviour extends FSMBehaviour
{
	public AuctionManagementBehaviour(MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
		
		// Register states
		// The last state must call myMarketAgent.setIsDone(true);
		
		// Register transitions
	}
}