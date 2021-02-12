package com.unilink.jira.script200

import com.atlassian.jira.rest.client.internal.async.AsynchronousJiraRestClientFactory
import java.net.URI
import kotlin.system.exitProcess

fun main()
{
    val username = System.getenv("JIRA_USERNAME")
    val password = System.getenv("JIRA_PASSWORD")
    val jiraUrl = System.getenv("JIRA_URL")
    val issueKey = System.getenv("JIRA_ISSUE")

    if (username == null || password == null || jiraUrl == null || issueKey == null)
        exitProcess(1)

    val restClient = AsynchronousJiraRestClientFactory().createWithBasicHttpAuthentication(URI(jiraUrl), username, password)


    val issue = restClient.issueClient.getIssue(issueKey).claim()
    val crnAreaRegex = Regex("([A-Z]\\d{6}\\s+C\\d{2})")
    val crnMatches = crnAreaRegex.findAll(issue.description!!)

    // Convert matches to a mapping of CRN to Probation Area
    val crnAreaMap = crnMatches.associate {
        val split = it.groupValues[1].split(Regex("\\s+"))
        Pair(split[0].trim(), split[1].trim())
    }

    val crnList = crnAreaMap.keys.joinToString {"'$it'"}
    val selectTemplate = "select * from MISSING_ALLOCATED_OFFENDERS m join offender o on o.offender_id = m.offender_id and crn in ($crnList);"
    println(selectTemplate)
    println()

    // Generate missing_allocated_offenders insert statements.
    crnAreaMap.forEach { (crn, area) ->
        val insertTemplate = "insert into missing_allocated_offenders (offender_id, provider_id, exported) values " +
                "((select offender_id from offender where crn = '$crn')," +
                "(select probation_area_id from probation_area where code = '$area'), 0);"
        println(insertTemplate)
    }

}
