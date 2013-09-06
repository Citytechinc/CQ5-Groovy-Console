package com.citytechinc.cq.groovyconsole.servlets

import com.citytechinc.cq.groovy.extension.services.OsgiComponentService
import com.citytechinc.cq.groovyconsole.services.GroovyConsoleConfigurationService
import com.day.cq.replication.Replicator
import com.day.cq.wcm.api.PageManager
import groovy.json.JsonSlurper
import org.osgi.framework.BundleContext
import spock.lang.Shared

import static com.citytechinc.cq.groovyconsole.servlets.ScriptPostServlet.SCRIPT_PARAM

class ScriptPostServletSpec extends AbstractServletSpec {

    @Shared servlet

    @Shared script

    def setupSpec() {
        servlet = new ScriptPostServlet()

        servlet.session = session
        servlet.resourceResolver = resourceResolver
        servlet.pageManager = Mock(PageManager)
        servlet.replicator = Mock(Replicator)
        servlet.componentService = Mock(OsgiComponentService)
        servlet.bundleContext = Mock(BundleContext)
        servlet.bundleContext = Mock(GroovyConsoleConfigurationService)

        script = getScriptAsString("Script")
    }

    def "run script"() {
        setup: "mock request with script parameter"
        def parameterMap = [(SCRIPT_PARAM): [script]]

        def request = requestBuilder.build {
            parameters parameterMap
        }

        def response = responseBuilder.build()

        when: "post to servlet"
        servlet.doPost(request, response)

        then: "script is executed"
        assertJsonResponse(response)
    }

    void assertJsonResponse(response) {
        def json = new JsonSlurper().parseText(response.output.toString())

        assert !json.executionResult
        assert json.outputText == "BEER\r\n"
        assert !json.stacktraceText
        assert json.runningTime
    }
}