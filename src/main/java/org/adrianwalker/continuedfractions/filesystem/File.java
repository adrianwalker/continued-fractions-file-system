package org.adrianwalker.continuedfractions.filesystem;

import java.io.Serializable;
import java.math.BigDecimal;

public final class File implements Serializable {

  private BigDecimal id;
  private int nv;
  private int dv;
  private BigDecimal sid;
  private int sdv;
  private int snv;
  private int level;
  private String name;
  private long content;

  public File(
          final BigDecimal id, final int nv, final int dv,
          final BigDecimal sid, final int snv, final int sdv,
          final int level,
          final String name, final long content) {

    this.id = id;
    this.nv = nv;
    this.dv = dv;
    this.sid = sid;
    this.sdv = sdv;
    this.snv = snv;
    this.level = level;
    this.name = name;
    this.content = content;
  }

  public BigDecimal getId() {
    return id;
  }

  public void setId(final BigDecimal id) {
    this.id = id;
  }

  public int getNv() {
    return nv;
  }

  public void setNv(final int nv) {
    this.nv = nv;
  }

  public int getDv() {
    return dv;
  }

  public void setDv(final int dv) {
    this.dv = dv;
  }

  public BigDecimal getSid() {
    return sid;
  }

  public void setSid(final BigDecimal sid) {
    this.sid = sid;
  }

  public int getSdv() {
    return sdv;
  }

  public void setSdv(final int sdv) {
    this.sdv = sdv;
  }

  public int getSnv() {
    return snv;
  }

  public void setSnv(final int snv) {
    this.snv = snv;
  }

  public int getLevel() {
    return level;
  }

  public void setLevel(final int level) {
    this.level = level;
  }

  public String getName() {
    return name;
  }

  public void setName(final String name) {
    this.name = name;
  }

  public long getContent() {
    return content;
  }

  public void setContent(long content) {
    this.content = content;
  }
}
