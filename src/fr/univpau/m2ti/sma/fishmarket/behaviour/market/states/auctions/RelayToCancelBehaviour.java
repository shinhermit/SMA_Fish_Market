package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
import jade.core.AID;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class RelayToCancelBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public RelayToCancelBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		ACLMessage toRelay = this.myFSM.getRequest();
		
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
		toRelay.clearAllReceiver();
		
		for(AID subscriber : myMarketAgent.getSubscribers(
				this.myFSM.getAuctionId()))
		{
			toRelay.addReceiver(subscriber);
		}
		
		// Put back message topic
		toRelay.addReceiver(
				RunningAuctionManagementBehaviour.MESSAGE_TOPIC);
		
		super.myAgent.send(toRelay);
		
		// Delete request
		this.myFSM.setRequest(null);
		
		// Close auction
		myMarketAgent.setAuctionStatus(
				this.myFSM.getAuctionId(), Auction.STATUS_CANCELLED);
	}
}
