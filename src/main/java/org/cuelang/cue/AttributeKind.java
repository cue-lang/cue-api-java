// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//	http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.cuelang.cue;

import static org.cuelang.libcue.cue_h.*;

public enum AttributeKind {
    FIELD(CUE_ATTR_FIELD()),
    DECLARATION(CUE_ATTR_DECL()),
    VALUE(CUE_ATTR_VALUE());

    private final int kind;

    public int kind() {
        return kind;
    }

    AttributeKind(int k) {
        this.kind = k;
    }
}