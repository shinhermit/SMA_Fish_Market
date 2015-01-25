package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
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
	private RunningAuctionMarketFSMBehaviour myFSM;
	
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
			RunningAuctionMarketFSMBehaviour myFSM)
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
		
		ACLMessage request = this.myFSM.getRequest();
		
		// Update auction price
		float price = Float.parseFloat(
				(String)request.getContent());
		
		myMarketAgent.setAuctionPrice(
				this.myFSM.getAuctionId(), price);
		
		// RELAY
		ACLMessage toRelay = new ACLMessage(
				request.getPerformative());
		
		// Add all subscribers as receivers
		for(AID subscriber : myMarketAgent.getSubscribers(
				this.myFSM.getAuctionId()))
		{
			toRelay.addReceiver(subscriber);
		}
		
		// Message topic
		toRelay.addReceiver(
				RunningAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Conversation ID
		toRelay.setConversationId(
				request.getConversationId());
		
		// Content (price)
		toRelay.setContent(request.getContent());
		
		// Send
		myMarketAgent.send(toRelay);
		
		// delete request
		this.myFSM.setRequest(null);
		
		// Update GUI
		myMarketAgent.refreshView();
	}
}
