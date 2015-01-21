package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionBehaviour;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
public class TerminateFailureBehaviour extends OneShotBehaviour
{
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateFailureBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionBehaviour myFSM)
	{
		super(mySellerAgent);
	}
	
	@Override
	public void action()
	{
		
	}
}
