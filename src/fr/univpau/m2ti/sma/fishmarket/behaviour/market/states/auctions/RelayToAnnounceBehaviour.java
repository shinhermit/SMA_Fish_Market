package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionManagementBehaviour;
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
public class RelayToAnnounceBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private AuctionManagementBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public RelayToAnnounceBehaviour(
			MarketAgent myMarketAgent,
			AuctionManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		MarketAgent myMarketAgent = (MarketAgent)
				super.myAgent;
		
		ACLMessage toRelay = this.myFSM.getRequest();
		
		// Update auction price
		float price = Float.parseFloat(
				(String)toRelay.getContent());
		
		myMarketAgent.setAuctionPrice(
				this.myFSM.getAuctionId(), price);
		
		// Relay
		toRelay.clearAllReceiver();
		
		// Put back message topic
		toRelay.addReceiver(
				AuctionManagementBehaviour.MESSAGE_TOPIC);
		
		// Add all subscribers as receivers
		for(AID subscriber : myMarketAgent.getSubscribers(
				this.myFSM.getAuctionId()))
		{
			toRelay.addReceiver(subscriber);
		}
		
		// Send
		myMarketAgent.send(toRelay);
		
		// delete request
		this.myFSM.setRequest(null);
	}
}
