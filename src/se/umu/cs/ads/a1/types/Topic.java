package se.umu.cs.ads.a1.types;

import se.umu.cs.ads.a1.adts.AbstractStringType;
import se.umu.cs.ads.a1.util.Util;

public class Topic extends AbstractStringType
{
  private final boolean wildcard;


  //----------------------------------------------------------
  public Topic (String value)
  {
    super(Util.stripWildcard(value));

    if (!Util.validateTopic(value))
      throw new IllegalArgumentException(value);

    this.wildcard = Util.endsWithWildcard(value);
  }

  //----------------------------------------------------------
  public boolean getWildcard ()
  {
    return wildcard;
  }

  //----------------------------------------------------------
  public boolean match (Topic topic)
  {
    return match(this,topic);
  }

  //----------------------------------------------------------
  @Override
  public String toString ()
  {
    return Util.toWildcardString(value,wildcard);
  }


  //----------------------------------------------------------
  //----------------------------------------------------------
  // matches a pattern to a topic
  // NOTE: pattern may contain a wildcard '*'
  public static boolean match (Topic pattern, Topic data)
  {
    return pattern.wildcard ? data.value.startsWith(pattern.value) : data.value.equals(pattern.value);
  }
}
