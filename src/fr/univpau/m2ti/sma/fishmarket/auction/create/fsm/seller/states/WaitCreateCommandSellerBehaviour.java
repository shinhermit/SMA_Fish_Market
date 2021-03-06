package fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.seller.states;

import fr.univpau.m2ti.sma.fishmarket.agent.SellerAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.create.fsm.CreateAuctionSellerFSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
public class WaitCreateCommandSellerBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this behaviour is to be added. */
	private CreateAuctionSellerFSMBehaviour myFSM;
	
	/** The transition which will be selected. */
	private int transition;
	
	/** the duration of one blocking cycle in order to wait for suscribers. */
	private static final long WAIT_CREATE_COMMAND_CYCLE_DURATION = 500; // 1 sec
	
	/**
	 * Creates a behaviour which represents a state of the FSM behaviour of a seller agent.
	 * 
	 * @param mySellerAgent the seller agent to which the FSM is to be added.
	 * @param myFSM the FSM behaviour to which this behaviour is to be added.
	 */
	public WaitCreateCommandSellerBehaviour(
			SellerAgent mySellerAgent,
			CreateAuctionSellerFSMBehaviour myFSM)
	{
		super(mySellerAgent);
		
		this.myFSM = myFSM;
	}
	
	@Override
	public void action()
	{
		SellerAgent mySellerAgent = (SellerAgent) super.myAgent;
		
		if(mySellerAgent.isCreateCommandReceived())
		{
			if(mySellerAgent.lookupMarketAgent())
			{
				// DEBUG
				System.out.println("Seller: setting transition to request creation !");
				
				this.transition =
						CreateAuctionSellerFSMBehaviour.TRANSITION_TO_REQUEST_CREATION;
			}
			else
			{
				mySellerAgent.notifyMarketNotFound();
				
				this.transition =
						CreateAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_CREATE_COMMAND;
				
				this.myFSM.block(WAIT_CREATE_COMMAND_CYCLE_DURATION);
			}
		}
		else
		{
			this.transition =
					CreateAuctionSellerFSMBehaviour.TRANSITION_TO_WAIT_CREATE_COMMAND;
			
			this.myFSM.block(WAIT_CREATE_COMMAND_CYCLE_DURATION);
		}
	}

	@Override
	public int onEnd()
	{
		return this.transition;
	}
}
