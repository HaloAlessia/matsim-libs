package run;

import java.util.TreeMap;

import miniscenario.AgentInteraction;
import occupancy.FacilitiesOccupancyCalculator;
import occupancy.FacilityOccupancy;

import org.matsim.api.core.v01.Id;
import org.matsim.api.core.v01.network.Network;
import org.matsim.core.api.experimental.facilities.ActivityFacilities;
import org.matsim.core.config.Config;
import org.matsim.core.config.groups.PlanCalcScoreConfigGroup;
import org.matsim.core.controler.Controler;
import org.matsim.utils.objectattributes.ObjectAttributes;
import org.matsim.utils.objectattributes.ObjectAttributesXmlReader;
import scoring.AgentInteractionScoringFunctionFactory;

public class RunControler extends Controler {
private TreeMap<Id, FacilityOccupancy> facilityOccupancies = new TreeMap<Id, FacilityOccupancy>();;
	

	public RunControler(final String[] args) {
		super(args);
		super.setOverwriteFiles(true) ;
	}

	public static void main(String[] args) {
		RunControler controler = new RunControler( args ) ;	
		controler.run();
	}
	
	protected void setUp() {

		this.setCreateGraphs(true);
		this.setDumpDataAtEnd(false);
		this.setWriteEventsInterval(0);
		
	    ObjectAttributes attributes = new ObjectAttributes();
	    ObjectAttributesXmlReader attributesReader = new ObjectAttributesXmlReader(attributes);
		attributesReader.parse("./input/facilityAttributes.xml");
		
		// get objects that are required as parameter for the AgentInteractionScoringFunctionFactory 
		PlanCalcScoreConfigGroup planCalcScoreConfigGroup = this.getConfig().planCalcScore();
		ActivityFacilities facilities = this.getFacilities();
		Network network = this.getNetwork();

		// create the AgentInteractionScoringFunctionFactory
		AgentInteractionScoringFunctionFactory factory = new AgentInteractionScoringFunctionFactory(planCalcScoreConfigGroup, facilities, network, Double.parseDouble(this.getConfig().locationchoice().getScaleFactor()), facilityOccupancies, attributes);

		// set the AgentInteractionScoringFunctionFactory as default in the controler 
		this.setScoringFunctionFactory(factory);
	    super.setUp();	
		
		addControlerListener(new FacilitiesOccupancyCalculator(this.facilityOccupancies, AgentInteraction.numberOfTimeBins, AgentInteraction.scaleNumberOfPersons));		    
	}
}
