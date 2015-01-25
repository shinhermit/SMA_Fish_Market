package fr.univpau.m2ti.sma.fishmarket.auction.subscribe.fsm.market.states;

import fr.univpau.m2ti.sma.fishmarket.agent.MarketAgent;
import jade.core.behaviours.OneShotBehaviour;

@SuppressWarnings("serial")
/**
 * A behaviour which is to be associated with a state of the marker agent's FSM behaviour.
 * 
 * @author Josuah Aron
 *
 */
public class TerminateSubscriptionsMarketBehaviour extends OneShotBehaviour
{
	/**
	 * Creates a terminating behaviour which is to be associated with a state of the 
	 * market agent's FSM behaviour.
	 * 
	 * @param myMarketAgent
	 * 			the market agent of which FSM behavior's state this behaviour is to be associated.
	 */
	public TerminateSubscriptionsMarketBehaviour(
			MarketAgent myMarketAgent)
	{
		super(myMarketAgent);
	}

	@Override
	public void action()
	{
		((MarketAgent)super.myAgent).setIsDone(true);
		// Say bye !
	}
}
