package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.auctions;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.RunningAuctionManagementFSMBehaviour;
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
public class WaitRepBidBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionManagementFSMBehaviour myFSM;
	
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
	public WaitRepBidBehaviour(
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
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// DEBUG
		System.out.println("Market: checking messages for rep bid");
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				this.messageFilter);
		
		if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			int performative = mess.getPerformative();
			
			if( performative ==
					FishMarket.Performatives.REP_BID)
			{
				// DEBUG
				System.out.println("Market: setting transition to relay rep bid nok");
				
				this.transition =
						RunningAuctionManagementFSMBehaviour.TRANSITION_TO_RELAY_REP_BID_NOK;
				
				String content = (String) mess.getContent();
				
				if(content != null)
				{
					boolean repBidOk = Boolean.parseBoolean(content);
					
					if(repBidOk)
					{
						// DEBUG
						System.out.println("Market: setting transition to relay rep bid ok");
						
						this.transition =
									RunningAuctionManagementFSMBehaviour.TRANSITION_TO_RELAY_REP_BID_OK;
					}
				}
			}
			
			else if(performative ==
					FishMarket.Performatives.TO_BID)
			{
				// DEBUG
				System.out.println("Market: setting transition to relay bid");
				
				this.transition =
						RunningAuctionManagementFSMBehaviour
						.TRANSITION_TO_RELAY_BID;
			}
			
			else if(performative ==
					FishMarket.Performatives.TO_CANCEL)
			{
				// DEBUG
				System.out.println("Market: setting transition to cancel");
				
				this.transition =
						RunningAuctionManagementFSMBehaviour
						.TRANSITION_TO_CANCEL;
			}
		}
		else
		{
			// Continue to wait
			this.transition = RunningAuctionManagementFSMBehaviour.
					TRANSITION_TO_WAIT_REP_BID;
			
			// DEBUG
			System.out.println("Market: setting transition to wait rep bid");
			
			this.myFSM.block();
		}
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
								FishMarket.Performatives.REP_BID),
						MessageTemplate.or(
								MessageTemplate.MatchPerformative(
										FishMarket.Performatives.TO_BID),
								MessageTemplate.MatchPerformative(
										FishMarket.Performatives.TO_CANCEL))));
	}
}
