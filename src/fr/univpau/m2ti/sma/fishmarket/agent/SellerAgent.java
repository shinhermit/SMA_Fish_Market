package fr.univpau.m2ti.sma.fishmarket.agent;

import java.util.logging.Logger;

import fr.univpau.m2ti.sma.fishmarket.behaviour.seller.SellerBehaviour;

@SuppressWarnings("serial")
public class SellerAgent extends AbstractMarketUser
{
	protected Logger logger =
			Logger.getLogger(AbstractMarketUser.class.getName());

	public SellerAgent() {
		// TODO Auto-generated constructor stub
		System.err.println("before setup => -1");
		this.setup();
	}
	
	@Override
	protected void setup()
	{
		System.err.println("in setup => 0");
		// Look up market
		//super.setup();
		System.err.println("in setup => 1");
		SellerBehaviour sb = new SellerBehaviour(this);
		System.err.println("in setup => 2");
		this.addBehaviour(sb);
		System.err.println("in setup => 3");
		
		
	}
	
	@Override
	protected void takeDown()
	{
		// Cancel auctions
		super.takeDown();
	}
	
}
