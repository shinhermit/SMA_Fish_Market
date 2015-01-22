package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the market agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class WaitToAnnounceBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementBehaviour myFSM;
	
	/** The transition which will be selected. */
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
	public WaitToAnnounceBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
		
		this.messageFilter =
				this.createMessageFilter();
	}
	
	@Override
	public void action()
	{
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
		// Receive messages
		ACLMessage mess = myMarketAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			this.restart();
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_ANNOUNCE)
			{
				this.myFSM.setRequest(mess);
				
				this.transition = RunningAuctionManagementBehaviour.
						TRANSITION_TO_RELAY_ANNOUNCE;
			}
			else
			{
				this.transition = RunningAuctionManagementBehaviour.
						TRANSITION_TO_CANCEL;
			}
		}
		else
		{
			this.transition = RunningAuctionManagementBehaviour.
					TRANSITION_TO_WAIT_TO_ANNOUNCE;
		}
	}
	
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
								FishMarket.Performatives.TO_ANNOUNCE),
						MessageTemplate.MatchPerformative(
								FishMarket.Performatives.TO_CANCEL)));
	}
}
