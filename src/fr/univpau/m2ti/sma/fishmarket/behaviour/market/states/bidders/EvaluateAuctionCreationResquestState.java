package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
public class EvaluateAuctionCreationResquestState extends OneShotBehaviour
{
	public EvaluateAuctionCreationResquestState(MarketAgent myMarketAgent)
	{
		
	}
	
	@Override
	public void action()
	{
		
	}
	
	@Override
	public int onEnd()
	{
		// refuse, return SellerManagementBehaviour.TRANSITION_REFUSE_AUCTION_CREATION_REQUEST
		// else return SellerManagementBehaviour.TRANSITION_CONFIRM_AUCTION_CREATION
		return 0;
	}
}