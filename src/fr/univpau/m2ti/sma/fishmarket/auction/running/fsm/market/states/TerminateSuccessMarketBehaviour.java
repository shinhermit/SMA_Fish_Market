package fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import fr.univpau.m2ti.sma.fishmarket.auction.running.fsm.RunningAuctionMarketFSMBehaviour;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class TerminateSuccessMarketBehaviour extends OneShotBehaviour
{
	/** The FSM behaviour to which this representative state is attached. */
	private RunningAuctionMarketFSMBehaviour myFSM;
	
	/**
	 * Creates a terminating behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 * @param myFSM the FSM behaviour of which this behaviour represents a state.
	 */
	public TerminateSuccessMarketBehaviour(
			MarketAgent myMarketAgent,
			RunningAuctionMarketFSMBehaviour myFSM)
	{
		super(myMarketAgent);
		
		this.myFSM = myFSM;
	}

	@Override
	public void action()
	{
		MarketAgent myMarketAgent =
				(MarketAgent) super.myAgent;
		
//		myMarketAgent.deleteAuction(this.myFSM.getAuctionId());
		
		// Update GUI
		myMarketAgent.refreshView();
		
		myMarketAgent.removeBehaviour(this.myFSM);
		
		// Say bye !
	}
}
