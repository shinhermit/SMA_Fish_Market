package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.RegisterAuctionBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitResponseBehaviour extends Behaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private RegisterAuctionBehaviour myFSM;
	
	/** Tells whether this behaviour has ended it's task or not. */
	private boolean isDone;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					RegisterAuctionBehaviour.MESSAGE_FILTER,
					MessageTemplate.or(
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_ACCEPT),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.TO_REFUSE)));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitResponseBehaviour(
			SellerAgent mySellerAgent,
			RegisterAuctionBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		this.isDone = false;
		
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitResponseBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.myFSM.setResponse(mess);
			
			if(mess.getPerformative() ==
					FishMarket.Performatives.TO_ACCEPT)
			{
				this.transition =
						RegisterAuctionBehaviour
						.TRANSITION_TO_WAIT_BIDDERS;
			}
			else
			{
				this.transition =
						RegisterAuctionBehaviour
						.TRANSITION_TO_REQUEST_CREATION;
			}
			
			this.isDone = true;
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
}
