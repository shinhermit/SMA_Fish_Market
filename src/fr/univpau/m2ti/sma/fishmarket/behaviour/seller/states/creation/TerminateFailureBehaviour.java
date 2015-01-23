package fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.creation;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.CreateAuctionSellerFSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
public class TerminateFailureBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public TerminateFailureBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		this.myFSM.setRequestCount(0);
		
		// DEBUG
		System.out.println("Seller: Terminate failure !");
	}
}
