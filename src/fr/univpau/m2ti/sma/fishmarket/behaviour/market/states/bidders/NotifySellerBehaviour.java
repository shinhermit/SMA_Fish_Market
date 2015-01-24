package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.bidders;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.BidderSubscriptionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.CreateAuctionMarketFSMBehaviour;
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
public class NotifySellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private BidderSubscriptionMarketFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public NotifySellerBehaviour(
			MarketAgent myMarketAgent,
			BidderSubscriptionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		// Notify the seller (so he can start to announce).
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
		ACLMessage request = this.myFSM.getRequest();
		
		String auctionID = (String) request.getContent();
		
		ACLMessage notify = new ACLMessage(
				FishMarket.Performatives.TO_SUBSCRIBE);
		
		// Set topic
		notify.addReceiver(
				CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Inform conversation ID
		notify.setConversationId(auctionID);
		notify.setContent(auctionID);
		
		notify.addReceiver(
				myMarketAgent.getSeller(auctionID));
		
		myMarketAgent.send(notify);
		
		// Update GUI
		myMarketAgent.refreshView();
	}
}
