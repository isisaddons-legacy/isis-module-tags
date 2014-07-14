/*
 *
 *  Copyright 2012-2014 Eurocommercial Properties NV
 *
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.tags.dom;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.services.bookmark.Bookmark;
import org.apache.isis.applib.services.bookmark.BookmarkService;
import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;

import static org.hamcrest.CoreMatchers.*;

public class TagsTest {

    public static class Actions extends TagsTest {

        public static class TagFor extends TagsTest {

            @Rule
            public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

            private Tag tag;
            @Mock
            private Object mockCustomer;
            @Mock
            private DomainObjectContainer mockContainer;
            @Mock
            private BookmarkService mockBookmarkService;

            private Tags tags;

            @Before
            public void setup() {
                tag = new Tag();
                tag.setKey("theme");
                tag.setTaggedObject(mockCustomer);
                tag.setValue("lightTheme");

                tags = new Tags();
                tags.container = mockContainer;
                tags.bookmarkService = mockBookmarkService;
            }

            @Test
            public void whenTagNotNull_butTagValueIsNull_thenTagIsRemoved() {
                context.checking(new Expectations() {
                    {
                        oneOf(mockContainer).remove(tag);
                    }
                });

                tag = tags.tagFor(tag, mockCustomer, "someTag", null);
                Assert.assertThat(tag, is(nullValue()));
            }

            @Test
            public void whenTagNotNull_butTagValueIsEmptyString_thenTagIsRemoved() {
                context.checking(new Expectations() {
                    {
                        oneOf(mockContainer).remove(tag);
                    }
                });

                tag = tags.tagFor(tag, mockCustomer, "theme", "");
                Assert.assertThat(tag, is(nullValue()));
            }

            @Test
            public void whenTagNotNull_andTagValueIsNotNull_thenTagsValueIsUpdated() {
                tag = tags.tagFor(tag, mockCustomer, "theme", "darkTheme");
                Assert.assertThat(tag, is(not(nullValue())));
                Assert.assertThat(tag.getValue(), is("darkTheme"));
            }

            @Test
            public void whenTagIsNull_andTagValueIsNull_thenNothing() {
                tag = tags.tagFor(null, mockCustomer, "theme", null);
                Assert.assertThat(tag, is(nullValue()));
            }

            @Test
            public void whenTagIsNull_andTagValueIsEmptyString_thenNothing() {
                tag = tags.tagFor(null, mockCustomer, "theme", "");
                Assert.assertThat(tag, is(nullValue()));
            }

            @Test
            public void whenTagIsNull_andTagValueIsNotNull_thenTagCreatedAndSet() {
                final Tag newTag = new Tag();

                context.checking(new Expectations() {
                    {
                        oneOf(mockContainer).newTransientInstance(Tag.class);
                        will(returnValue(newTag));

                        oneOf(mockBookmarkService).bookmarkFor(mockCustomer);
                        will(returnValue(new Bookmark("CXS", "456")));

                        oneOf(mockContainer).persist(newTag);
                    }
                });

                tag = tags.tagFor(null, mockCustomer, "theme", "darkTheme");
                Assert.assertThat(tag, is(not(nullValue())));
                Assert.assertThat(tag.getTaggedObject(), is(mockCustomer));
                Assert.assertThat(tag.getKey(), is("theme"));
                Assert.assertThat(tag.getValue(), is("darkTheme"));
            }
        }
    }
}