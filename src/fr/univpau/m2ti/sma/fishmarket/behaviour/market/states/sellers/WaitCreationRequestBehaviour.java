package fr.univpau.m2ti.sma.fishmarket.behaviour.market.states.sellers;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.AuctionCreationManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class WaitCreationRequestBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private AuctionCreationManagementBehaviour myFSM;
	
	/** The selected next transition. */
	private int transition;

	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
				MessageTemplate.and(
					AuctionCreationManagementBehaviour.MESSAGE_FILTER,
					MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_CREATE));
	
	/**
	 * Creates a behaviour which is to be associated with a MarketAgent FSMBehaviour's state.
	 * 
	 * @param myMarketAgent
	 * 			the market agent to which the composite FSM behaviour
	 * 			(which contains the state to which this behaviour is associated) is added.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public WaitCreationRequestBehaviour(
			MarketAgent myMarketAgent,
			AuctionCreationManagementBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		// Delete any previous request
		this.myFSM.setRequest(null);
		
		// Wait that myAgent receives message
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitCreationRequestBehaviour.MESSAGE_FILTER);
		
		if( ((MarketAgent)myAgent).isDone() )
		{
			this.transition = AuctionCreationManagementBehaviour.
					TRANSITION_TO_TERMINATE;
			
			// Reset blocking state
			this.restart();
		}
		else if(mess != null)
		{
			this.myFSM.setRequest(mess);
			
			this.transition = AuctionCreationManagementBehaviour.
					TRANSITION_TO_EVALUATE_REQUEST;
			
			// Reset blocking state
			this.restart();
		}
		else
		{
			this.transition = AuctionCreationManagementBehaviour.
					TRANSITION_TO_WAIT_REQUEST;
		}
	}
	
	@Override
	public int onEnd()
	{
		return  this.transition;
	}
}
