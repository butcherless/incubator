package com.cmartin.domain

import org.neo4j.ogm.annotation.{GeneratedValue, Id, NodeEntity}

import scala.beans.BeanProperty

@NodeEntity
case class Person(@BeanProperty @Id @GeneratedValue id: java.lang.Long, @BeanProperty name: String)