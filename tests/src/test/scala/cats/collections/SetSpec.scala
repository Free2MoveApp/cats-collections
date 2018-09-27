package cats.collections
package tests

import cats.tests.CatsSuite

class SetSpec extends CatsSuite {
  test("set is always sorted")(forAll { (xs: List[Int]) =>
    val tree = xs.foldLeft(AvlSet.empty[Int])(_ + _)

    val ours: List[Int] = tree.toList
    val theirs: List[Int] = xs.to[Set].to[Array].sorted.to[List]

    ours should be (theirs)
  })

  import AvlSet._
  def balanced[A](t: AvlSet[A]): Boolean = t match {
    case BTNil() => true
    case Branch(_, l, r) =>
      java.lang.Math.abs(l.height - r.height) <= 1 && balanced(l) && balanced(r)

  }

  test("set is always balanced")(forAll { (xs: List[Int]) =>
    val tree = AvlSet(xs: _*)
    balanced(tree) should be(true)
  })

  test("set can delete")(forAll{ (xs: Map[Int,Boolean]) =>
    val tree = AvlSet(xs.keySet.toSeq: _*)
    val filtered = xs.foldLeft(tree)((t,i) => if(i._2) t else t.remove(i._1))

    val ours: Set[Int] = filtered.toList.to[Set]
    val theirs: Set[Int] = xs.collect{ case (i, true) => i }.to[Set]

    ours should be (theirs)
    balanced(filtered) should be(true)
  })

  test("contains works")(forAll{ (xs: Map[Int,Boolean]) =>
    val tree = xs.foldLeft[AvlSet[Int]](AvlSet.empty)((t,i) =>
      if(i._2) t + i._1 else t
    )

    xs.map{
      case (k,v) => tree.contains(k) == v
    }.foldLeft(true)(_ && _) should be(true)
  })

  test("find works")(forAll{ (xs: Map[Int,Boolean]) =>
    val tree = xs.foldLeft[AvlSet[Int]](AvlSet.empty)((t,i) =>
      if(i._2) t + i._1 else t
    )

    xs.map{
      case (k,v) => tree.find(_ == k).isDefined == v
    }.foldLeft(true)(_ && _) should be(true)
  })

  test("intersect is correct")(forAll{ (xs: Set[Int], ys: Set[Int]) =>
    val xt = AvlSet(xs.toSeq: _*)
    val yt = AvlSet(ys.toSeq: _*)

    (xt intersect yt).toScalaSet should be (xs intersect ys)
    (xt & yt).toScalaSet should be(xs intersect ys)
  })

  test("union is correct")(forAll{ (xs: Set[Int], ys: Set[Int]) =>
    val xt = AvlSet(xs.toSeq: _*)
    val yt = AvlSet(ys.toSeq: _*)

    (xt union yt).toScalaSet should be(xs union ys)
    (xt | yt).toScalaSet should be(xs union ys)
  })

  test("we can take the difference of sets")(forAll{ (xs: Set[Int], ys: Set[Int]) =>
    val xt = AvlSet(xs.toSeq: _*)
    val yt = AvlSet(ys.toSeq: _*)

    (xt diff yt).toScalaSet should be(xs diff ys)
    (xt - yt).toScalaSet should be(xs diff ys)
  })

  test("map works") (forAll{ (xs: Set[Int]) =>
    val f: Int => Int = _ + 1

    val xt = AvlSet(xs.toSeq: _*)

    (xt map f).toScalaSet should be(xs map f)
  })
}
