// Copyright (c) 2013-2017 Rob Norris
// This software is licensed under the MIT License (MIT).
// For more information see LICENSE or https://opensource.org/licenses/MIT

package doobie.hi

import doobie.enum.holdability.Holdability
import doobie.enum.fetchdirection.FetchDirection
import doobie.enum.resultsetconcurrency.ResultSetConcurrency
import doobie.enum.resultsettype.ResultSetType
import doobie.syntax.monaderror._

import java.sql.SQLWarning

import scala.Predef.intArrayOps

/**
 * Module of high-level constructors for `StatementIO` actions.
 * @group Modules
 */
object statement {

  /** @group Batching */
  def addBatch(sql: String): StatementIO[Unit] =
    FS.addBatch(sql)

  /** @group Batching */
  val clearBatch: StatementIO[Unit] =
    FS.clearBatch

  /** @group Execution */
  val executeBatch: StatementIO[List[Int]] =
    FS.executeBatch.map(_.toList)

  /** @group Execution */
  def executeQuery[A](sql: String)(k: ResultSetIO[A]): StatementIO[A] =
    FS.executeQuery(sql).flatMap(s => FS.embed(s, k guarantee FRS.close))

  /** @group Execution */
  def executeUpdate(sql: String): StatementIO[Int] =
    FS.executeUpdate(sql)

  /** @group Properties */
  val getFetchDirection: StatementIO[FetchDirection] =
    FS.getFetchDirection.map(FetchDirection.unsafeFromInt)

  /** @group Properties */
  val getFetchSize: StatementIO[Int] =
    FS.getFetchSize

  /** @group Results */
  def getGeneratedKeys[A](k: ResultSetIO[A]): StatementIO[A] =
    FS.getGeneratedKeys.flatMap(s => FS.embed(s, k guarantee FRS.close))

  /** @group Properties */
  val getMaxFieldSize: StatementIO[Int] =
    FS.getMaxFieldSize

  /** @group Properties */
  val getMaxRows: StatementIO[Int] =
    FS.getMaxRows

  // /** @group Batching */
  // def getMoreResults(a: Int): StatementIO[Boolean] =
  //   Predef.???

  /** @group Batching */
  val getMoreResults: StatementIO[Boolean] =
    FS.getMoreResults

  /** @group Properties */
  val getQueryTimeout: StatementIO[Int] =
    FS.getQueryTimeout

  /** @group Batching */
  def getResultSet[A](k: ResultSetIO[A]): StatementIO[A] =
    FS.getResultSet.flatMap(s => FS.embed(s, k))

  /** @group Properties */
  val getResultSetConcurrency: StatementIO[ResultSetConcurrency] =
    FS.getResultSetConcurrency.map(ResultSetConcurrency.unsafeFromInt)

  /** @group Properties */
  val getResultSetHoldability: StatementIO[Holdability] =
    FS.getResultSetHoldability.map(Holdability.unsafeFromInt)

  /** @group Properties */
  val getResultSetType: StatementIO[ResultSetType] =
    FS.getResultSetType.map(ResultSetType.unsafeFromInt)

  /** @group Results */
  val getUpdateCount: StatementIO[Int] =
    FS.getUpdateCount

  /** @group Results */
  val getWarnings: StatementIO[SQLWarning] =
    FS.getWarnings

  /** @group Properties */
  def setCursorName(name: String): StatementIO[Unit] =
    FS.setCursorName(name)

  /** @group Properties */
  def setEscapeProcessing(a: Boolean): StatementIO[Unit] =
    FS.setEscapeProcessing(a)

  /** @group Properties */
  def setFetchDirection(fd: FetchDirection): StatementIO[Unit] =
    FS.setFetchDirection(fd.toInt)

  /** @group Properties */
  def setFetchSize(n: Int): StatementIO[Unit] =
    FS.setFetchSize(n)

  /** @group Properties */
  def setMaxFieldSize(n: Int): StatementIO[Unit] =
    FS.setMaxFieldSize(n)

  /** @group Properties */
  def setMaxRows(n: Int): StatementIO[Unit] =
    FS.setMaxRows(n)

  /** @group Properties */
  def setQueryTimeout(a: Int): StatementIO[Unit] =
    FS.setQueryTimeout(a)

}
