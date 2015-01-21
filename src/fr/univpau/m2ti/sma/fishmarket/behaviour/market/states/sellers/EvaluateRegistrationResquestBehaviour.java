package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class EvaluateRegistrationResquestBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private SellerManagementBehaviour myFSM;
	
	/** The next selected transition. */
	private int transition;
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public EvaluateRegistrationResquestBehaviour(
			MarketAgent myMarketAgent,
			SellerManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)myAgent;
		
		ACLMessage request = this.myFSM.getRequest();
		
		String auctionId =
				AuctionManagementBehaviour.createAuctionId(
						request.getSender());
		
		Auction auction = new Auction(auctionId);
		
		if(! myMarketAgent.isRegisteredAuction(auction) )
		{
			// Register auction
			auction.setStatus(
					Auction.STATUS_RUNNING);
			
			myMarketAgent.registerAuction(
					auction, request.getSender());
			
			request.setConversationId(auctionId);
			
			// Next transition
			this.transition =
					SellerManagementBehaviour.TRANSITION_TO_CONFIRM_REGISTRATION;
		}
		else
		{
			// Reply refuse
			ACLMessage reply =
					this.myFSM.getRequest().createReply();
			
			reply.setPerformative(
					FishMarket.Performatives.TO_REFUSE);
			
			super.myAgent.send(reply);
			
			// Next transition
			this.transition =
					SellerManagementBehaviour.TRANSITION_TO_WAIT_REQUEST;
			
			this.myFSM.setRequest(null);
		}
	}
	
	@Override
	public int onEnd()
	{
		return this.transition;
	}
}