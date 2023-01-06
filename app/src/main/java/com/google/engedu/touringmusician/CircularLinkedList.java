/* Copyright 2016 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.google.engedu.touringmusician;


import android.graphics.Paint;
import android.graphics.Point;

import java.util.Iterator;

public class CircularLinkedList implements Iterable<Point> {


    private class Node {
        Point point;
        Node prev, next;

        Node(Point point, Node prev, Node next){
            this.point = point;
            this.next = next;
            this.prev = prev;
        }
    }
    private Paint paint;
    Node head;
    int size;

    public CircularLinkedList(Paint paint){
        this.paint = paint;
    }

    public CircularLinkedList(){
        super();
    }

    public Paint getPaint() {
        return paint;
    }

    public void insertBeginning(Point p) {

        Node newNode = new Node(p, null, null);
        size++;
        // empty list
        if (head == null){
            head = newNode;
            head.next = head;
            head.prev = head;
            return;
        }

        // one elem list
        if (head.next == head){
            Node oldHead = head;
            newNode.next = oldHead;
            newNode.prev = oldHead;
            oldHead.prev = newNode;
            oldHead.next = newNode;
            head = newNode;
            return;
        }

        // multi elem list
        Node lastNode = head.prev;
        Node oldHead = head;
        lastNode.next = newNode;
        newNode.next = oldHead;
        newNode.prev = lastNode;
        oldHead.prev = newNode;
        head = newNode;

    }

    private float distanceBetween(Point from, Point to) {
        return (float) Math.sqrt(Math.pow(from.y-to.y, 2) + Math.pow(from.x-to.x, 2));
    }

    public float totalDistance() {
        float total = 0;
        if (head == null || head.next == head)
            return 0;

        // loop through list while keeping track of previous point
        Point prevPoint = null;

        for (Point p : this){
            // if first elem, save as previous elem and continue
            if (prevPoint == null){
                prevPoint = p;
                continue;
            }
            total += distanceBetween(prevPoint, p);
            prevPoint = p;
        }
        // post processing: calculate the distance between last tour spot to initial tour spot
        total += distanceBetween(prevPoint, head.point);

        return total;
    }

    public void insertNearest(Point newPoint) {
        Node newNode = new Node(newPoint, null, null);
        // empty list
        if (head == null) {
            head = newNode;
            head.next = head;
            head.prev = head;
            return;
        }

        // if single elem list
        if (head.next == head) {
            newNode.prev = head;
            newNode.next = head;
            head.next = newNode;
            head.prev = newNode;
            return;
        }

        //multi elem list
        Node nearestNode = getNearestNodeToPoint(newPoint);
        Node oldNext = nearestNode.next;

        newNode.prev = nearestNode;
        newNode.next = oldNext;

        nearestNode.next = newNode;

        oldNext.prev = newNode;


    }

    public Node getNearestNodeToPoint(Point nearestTo){

        double minDistance = Double.POSITIVE_INFINITY;
        Node targetNode = null;
        Node traverse = head;

        while (traverse != null) {
            double distance = distanceBetween(traverse.point, nearestTo);

            if (distance < minDistance){
                minDistance = distance;
                targetNode = traverse;
            }
            traverse = traverse.next;
            if (traverse == head) break;
        }


        return targetNode;
    }

    /**
     * Find a node such that, if a new node is inserted after it,
     * the total distance travelled would be minimal
     * @param p
     * @return
     */
    public Node getNodeWithLeastDistance(Point p){

        double minDeltaDistance = Double.POSITIVE_INFINITY;

        Node targetNode = null;
        Node curr = head;
        Node next = curr.next;

        while (next != head) {
            double originalDistance = distanceBetween(curr.point, next.point);
            double newDistance =
                    distanceBetween(curr.point, p) +
                    distanceBetween(p, next.point);
            double localDeltaDiff = Math.abs(newDistance - originalDistance);
            if (localDeltaDiff < minDeltaDistance){
                minDeltaDistance = localDeltaDiff;
                targetNode = curr;
            }
            curr = next;
            next = next.next;

        }
        return targetNode;
    }

    public void insertSmallest(Point p) {
        Node newNode = new Node(p, null, null);

        if (head == null){
            head = newNode;
            head.next = head;
            head.prev = head;
            return;
        }

        if (head.next == head){
            newNode.next = head;
            newNode.prev = head;

            head.next = newNode;
            head.prev = newNode;
            return;
        }


        // multi-elem list
        Node bestNode = getNodeWithLeastDistance(p);
        // add after
        Node oldNext = bestNode.next;

        bestNode.next = newNode;

        oldNext.prev = newNode;

        newNode.prev = bestNode;
        newNode.next = oldNext;



    }

    public void reset() {
        head = null;
    }

    public int getSize() {
        return size;
    }

    private class CircularLinkedListIterator implements Iterator<Point> {

        Node current;

        public CircularLinkedListIterator() {
            current = head;
        }

        @Override
        public boolean hasNext() {
            return (current != null);
        }

        @Override
        public Point next() {
            Point toReturn = current.point;
            current = current.next;
            if (current == head) {
                current = null;
            }
            return toReturn;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }
    }



    @Override
    public Iterator<Point> iterator() {
        return new CircularLinkedListIterator();
    }




}
