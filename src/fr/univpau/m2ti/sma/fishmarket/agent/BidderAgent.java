package fr.univpau.m2ti.sma.fishmarket.agent;

import java.util.logging.Logger;

@SuppressWarnings("serial")
public class BidderAgent extends AbstractMarketUser
{
	protected Logger logger =
			Logger.getLogger(AbstractMarketUser.class.getName());

	@Override
	protected void setup()
	{
		// Look up market
		super.setup();
	}
	
	@Override
	protected void takeDown()
	{
		// Cancel auctions
		super.takeDown();
	}
}
