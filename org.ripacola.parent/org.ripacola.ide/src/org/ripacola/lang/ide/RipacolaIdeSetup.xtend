/*
 * generated by Xtext 2.17.1
 */
package org.ripacola.lang.ide

import com.google.inject.Guice
import org.eclipse.xtext.util.Modules2
import org.ripacola.lang.RipacolaRuntimeModule
import org.ripacola.lang.RipacolaStandaloneSetup

/**
 * Initialization support for running Xtext languages as language servers.
 */
class RipacolaIdeSetup extends RipacolaStandaloneSetup {

	override createInjector() {
		Guice.createInjector(Modules2.mixin(new RipacolaRuntimeModule, new RipacolaIdeModule))
	}
	
}
