package webclient;

import org.restlet.resource.ClientResource;


import service.GeneratorUtilities;


public class GeneratorUtilitiesClient {
	
		public static void main (String [] args) throws Exception {

			//should we make constructor take in the dates?
			//need to make it get actual GeneratorUtilitiess
			GeneratorUtilities genUtils = new GeneratorUtilities(0.5,0.25);

			GeneratorUtilities newGenUtils = null;
			
			ClientResource clientResource = new ClientResource("http://localhost:8111/");
	        
			GeneratorUtilitiesResource proxy = clientResource.wrap(GeneratorUtilitiesResource.class);
			System.out.println("Proxy made");
			System.out.println(genUtils.getMinimumConfidenceLevel());
			proxy.store(genUtils);
			newGenUtils = proxy.retrieve();

			if (newGenUtils != null) {
				System.out.println(newGenUtils);
				System.out.println("MSL: " + newGenUtils.getMinimumSupportLevel());
	            System.out.println("MCL: " + newGenUtils.getMinimumConfidenceLevel());
	         

	            
			}
			else {
				System.out.println("got null GeneratorUtilities");
			}
		}
	
}
