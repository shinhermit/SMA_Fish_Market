package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
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
	private RunningAuctionManagementFSMBehaviour myFSM;
	
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
			RunningAuctionManagementFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		// DEBUG
		System.out.println("Market: relaying to announce !");
		
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
		
		// Add all subscribers as receivers
		for(AID subscriber : myMarketAgent.getSubscribers(
				this.myFSM.getAuctionId()))
		{
			toRelay.addReceiver(subscriber);
		}
		
		// Put back message topic
		toRelay.addReceiver(
				RunningAuctionManagementFSMBehaviour.MESSAGE_TOPIC);
		
		// Send
		myMarketAgent.send(toRelay);
		
		// delete request
		this.myFSM.setRequest(null);
	}
}
