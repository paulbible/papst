/**
 * These tests were kindly supplied by github user gmungoc
 */
package papst;

import static org.testng.Assert.assertEquals;

import java.util.Vector;

import org.testng.annotations.Test;

import papst.regions.Region;
import papst.regions.SimpleRegion;

public class NCListTest
{
  @Test
  public void testGetRegionsInRange()
  {
	//System.out.println("========= Test 1 ===========");
    Vector<Region> regions = new Vector<Region>();
    regions.add(new SimpleRegion(100, 500));
    regions.add(new SimpleRegion(200, 300));

    NCList ncl = new NCList(regions);

    Vector<Region> overlap = ncl.getRegionsInRange(400, 600);
    assertEquals(overlap.size(), 1); // fails - two regions returned
    assertEquals(overlap.get(0).getStart(), 100);
    assertEquals(overlap.get(0).getEnd(), 500);
  }
  
  @Test
  public void testGetRegionsInRange2()
  {
	//System.out.println("========= Test 2 ===========");
    Vector<Region> regions = new Vector<Region>();
    regions.add(new SimpleRegion(100, 400));
    regions.add(new SimpleRegion(200, 500));

    NCList ncl = new NCList(regions);

    Vector<Region> overlap = ncl.getRegionsInRange(300, 300);
    assertEquals(overlap.size(), 2); // fixed
    overlap = ncl.getRegionsInRange(0, 150);
    assertEquals(overlap.size(), 1);
    overlap = ncl.getRegionsInRange(300, 450);
    assertEquals(overlap.size(), 2);
    overlap = ncl.getRegionsInRange(0, 50);
    assertEquals(overlap.size(), 0);
    overlap = ncl.getRegionsInRange(0, 550);
    assertEquals(overlap.size(), 2);
  }
  
  @Test
  public void testGetRegionsInRange3()
  {
	//System.out.println("========= Test 3 ===========");
    Vector<Region> regions = new Vector<Region>();
    regions.add(new SimpleRegion(100, 300));
    regions.add(new SimpleRegion(200, 400));
    regions.add(new SimpleRegion(600, 800));
    regions.add(new SimpleRegion(700, 900));
    regions.add(new SimpleRegion(800, 1000));

    NCList ncl = new NCList(regions);

    Vector<Region> overlap = ncl.getRegionsInRange(300, 300);
    assertEquals(overlap.size(), 2);
    
    overlap = ncl.getRegionsInRange(300, 450);
    assertEquals(overlap.size(), 2);
    
    overlap = ncl.getRegionsInRange(450, 550);
    assertEquals(overlap.size(), 0);
    
    overlap = ncl.getRegionsInRange(0, 1000);
    assertEquals(overlap.size(), 5);
    
    overlap = ncl.getRegionsInRange(650, 750);
    assertEquals(overlap.size(), 2);
    
    overlap = ncl.getRegionsInRange(350, 650);
    assertEquals(overlap.size(), 2);
  }
}
