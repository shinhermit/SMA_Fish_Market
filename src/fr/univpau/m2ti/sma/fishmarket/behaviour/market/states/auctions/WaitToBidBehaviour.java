package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class WaitToBidBehaviour extends Behaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementFSMBehaviour myFSM;
	
	/** Tells whether this behaviour is over or not. Over when an auction creation request has been received.*/
	private boolean isDone;
	
	/** Will hold the selected transition among those to the next possible states. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private final MessageTemplate messageFilter;
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public WaitToBidBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionManagementFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter = this.createMessageFilter();
	}
	
	@Override
	public void action()
	{
		this.isDone = false;
		
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			this.isDone = true;
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_BID)
			{
				this.transition =
						RunningAuctionManagementFSMBehaviour
						.TRANSITION_TO_RELAY_BID;
			}
			else if(mess.getPerformative() ==
					FishMarket.Performatives.TO_ANNOUNCE)
			{
				this.transition =
						RunningAuctionManagementFSMBehaviour
						.TRANSITION_TO_RELAY_ANNOUNCE;
			}
			else
			{
				this.transition =
						RunningAuctionManagementFSMBehaviour
						.TRANSITION_TO_CANCEL;
			}
		}
	}

	@Override
	public boolean done()
	{
		return this.isDone;
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
	
	/**
	 * Creates a filter for incoming message.
	 * 
	 * @return the filter for incoming messages.
	 */
	private MessageTemplate createMessageFilter()
	{
		return MessageTemplate.and(
				this.myFSM.getMessageFilter(),
				MessageTemplate.or(
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_BID),
						MessageTemplate.or(
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_ANNOUNCE),
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_CANCEL))));
	}
}
