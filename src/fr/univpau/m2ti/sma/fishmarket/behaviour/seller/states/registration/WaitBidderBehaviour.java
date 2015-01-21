package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.registration;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.market.SellerManagementBehaviour;
import fr.univpau.m2ti.sma.fishmarket.message.FishMarket;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

@SuppressWarnings("serial")
public class WaitBidderBehaviour extends Behaviour
{
	/** Tells whether this behaviour has ended it's task or not. */
	private boolean isDone;
	
	/** Allows filtering incoming messages. */
	private static final MessageTemplate MESSAGE_FILTER =
			MessageTemplate.and(
					MessageTemplate.MatchTopic(
							SellerManagementBehaviour.MESSAGE_TOPIC),
							MessageTemplate.MatchPerformative(
									FishMarket.Performatives.CONFIRM_BIDDER_SUBSCRIPTION));
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitBidderBehaviour(
			SellerAgent mySellerAgent)
	{
		super(mySellerAgent);
	}
	
	@Override
	public void action()
	{
		this.isDone = false;
		
		this.block();
		
		// Receive messages
		ACLMessage mess = myAgent.receive(
				WaitBidderBehaviour.MESSAGE_FILTER);
		
		if(mess != null)
		{
			this.isDone = true;
		}
	}

	@Override
	public boolean done()
	{
		return this.isDone;
	}
}
