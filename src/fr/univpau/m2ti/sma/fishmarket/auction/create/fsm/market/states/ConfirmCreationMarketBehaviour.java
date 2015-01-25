package fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.protocol.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class ConfirmCreationMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private CreateAuctionMarketFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public ConfirmCreationMarketBehaviour(
			MarketAgent myMarketAgent,
			CreateAuctionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		ACLMessage request = this.myFSM.getRequest();
		
		MarketAgent myMarketAgent = (MarketAgent)
				super.myAgent;
		
		// Create FSM Behaviour to manage the auction
		myMarketAgent.addBehaviour(
				new RunningAuctionMarketFSMBehaviour(myMarketAgent,
						request.getSender(), request.getConversationId()));
		
		// Send reply
		ACLMessage reply = request.createReply();
		
		reply.setPerformative(
				FishMarket.Performatives.TO_ACCEPT);
		
		// Set topic
		reply.addReceiver(
				CreateAuctionMarketFSMBehaviour.MESSAGE_TOPIC);
		
		// Set conversation ID
		reply.setConversationId(request.getConversationId());
		
		myMarketAgent.send(reply);
		
		// Delete request
		this.myFSM.setRequest(null);
		
		// Update GUI
		myMarketAgent.refreshView();
	}
}