package fr.univpau.m2ti.sma.fishmarket.behaviour.seller;

import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.StateDecrasePriceAnnounce;
import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.states.StateInitialOrAfterPriceIncreaseAnnounce;
import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

@SuppressWarnings("serial")
public class SellerBehaviour extends FSMBehaviour
{
	
	private static final String STATE_INITIAL_OR_AFTER_PRICE_INCREASE_ANNOUNCE =
			"STATE_INITIAL_OR_AFTER_PRICE_INCREASE_ANNOUNCE";
	private static final String STATE_DECREASE_PRICE_ANNOUNCE =
			"STATE_DECREASE_PRICE_ANNOUNCE";
	

	
	public SellerBehaviour(Agent a) {
		super(a);
		
		StateInitialOrAfterPriceIncreaseAnnounce initialState = 
			new StateInitialOrAfterPriceIncreaseAnnounce();
		StateDecrasePriceAnnounce decreasingPriceState = 
			new StateDecrasePriceAnnounce();
		
		this.registerFirstState(
			initialState, 
			STATE_INITIAL_OR_AFTER_PRICE_INCREASE_ANNOUNCE
		);
				
		this.registerLastState(decreasingPriceState, STATE_DECREASE_PRICE_ANNOUNCE);
		
		this.registerDefaultTransition(
			STATE_INITIAL_OR_AFTER_PRICE_INCREASE_ANNOUNCE, 
			STATE_DECREASE_PRICE_ANNOUNCE
		);
		
		this.registerDefaultTransition(
			STATE_DECREASE_PRICE_ANNOUNCE,
			STATE_INITIAL_OR_AFTER_PRICE_INCREASE_ANNOUNCE
		);
	}
	
	@Override
	public int onEnd() {
		System.out.println("Exiting SellerBehaviour");
		myAgent.doDelete();
		return super.onEnd();
	}
}
