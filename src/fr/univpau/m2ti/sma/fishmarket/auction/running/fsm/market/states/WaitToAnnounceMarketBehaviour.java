package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import fr.univpau.m2ti.sma.fishmarket.data.Auction;
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
public class WaitToAnnounceMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionMarketFSMBehaviour myFSM;
	
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
	public WaitToAnnounceMarketBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionMarketFSMBehaviour myFSM)
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
		
		// DEBUG
		System.out.println("Market: checking messages for to announce");
		
		// Receive messages
		ACLMessage mess = super.myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_ANNOUNCE)
			{
				this.myFSM.setRequest(mess);
				
				// DEBUG
				System.out.println("Market: setting transition to relay to announce");
				
				this.transition = RunningAuctionMarketFSMBehaviour.
						TRANSITION_TO_RELAY_TO_ANNOUNCE;
			}
			else
			{
				// DEBUG
				System.out.println("Market: setting transition to cancel");
				
				this.transition = RunningAuctionMarketFSMBehaviour.
						TRANSITION_TO_CANCEL;
				
				((MarketAgent)super.myAgent).setAuctionStatus(
						this.myFSM.getAuctionId(),
						Auction.STATUS_CANCELLED);
			}
		}
		else
		{
			// Continue to wait
			this.transition = RunningAuctionMarketFSMBehaviour.
					TRANSITION_TO_WAIT_TO_ANNOUNCE;
			
			// DEBUG
			System.out.println("Market: setting transition to wait to announce");
			
			this.myFSM.block();
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
