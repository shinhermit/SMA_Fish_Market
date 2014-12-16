package fr.univpau.m2ti.sma.fishmarket.agent;

@SuppressWarnings("serial")
public class BidderAgent extends AbstractMarketUser
{
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
